(ns troubleshooter-bot.extensions.roles
  (:require [discord.bot :as bot]
            [discord.http :as bot-http]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]
            )
  )

; These don't currently persist

; Needs to be refactred to be by guild
(def available-roles (atom #{}))
(def guild-data (atom {}))

(defn- init-data
  "Loads up/refreshes data from the guild it was called from
  Users is broken for some reason."
  [{:keys [auth] :as client} {:keys [content] :as message}]
  (let [guild-id (get-in message [:channel :guild-id])
        users (bot-http/list-members auth guild-id :limit 100)
        roles (bot-http/get-roles auth guild-id)
        ]
    (pprint users)
    (swap! guild-data assoc guild-id {:roles roles :users users})
    (bot/say (format "Done. Total users: %d, total roles: %d"
                     (count (get-in @guild-data [guild-id :users]))
                     (count (get-in @guild-data [guild-id :roles]))
                     ))))

(defn- to-lower-underscore
  [s]
  (-> s
      (clojure.string/replace #" " "_")
      (clojure.string/lower-case)
      )
  )
(comment
  (to-lower-underscore "AbCd Ef gH")
  (pprint @guild-data)
  ; available-roles needs to get refactored to by done by guild
  (pprint @available-roles))


(defn- list-inner ; There's already a list*, let's not mess with it
  [{:keys [auth] :as client} {:keys [content] :as message}]
  (->> available-roles
       deref
       (interpose ", ")
       (apply str "Available roles:" \newline)
       bot/say
       ))
(defn- get-role
  "Gets role from all roles when both are lowercase and underscored, or nil"
  [guild-id s]
  (->> (filter (fn [r]
                 (= (to-lower-underscore s)
                    (to-lower-underscore (:name r))
                    ))
               (get-in @guild-data [guild-id :roles]))
       first
       )
  )
(defn- get-available-role
  "Gets role from available roles when both are lowercase and underscored, or nil"
  [guild-id s]
  (->> @available-roles
       (filter (fn [r]
                 (= (to-lower-underscore s)
                    (to-lower-underscore (:name r))
                    )))
       first
       )
  )

(defn- giveme*
  [{:keys [auth] :as client} {:keys [content] :as message}]
  (pprint message)
  (let [guild-id (get-in message [:channel :guild-id])
        role-name (-> content (clojure.string/split #"\s+") second)]
    (if-let [role (get-available-role guild-id role-name)]
      (do
        (bot-http/discord-request
          :add-user-role auth
          :guild guild-id
          :member (get-in message [:author :id])
          :role (:id role)
          )
        (bot/say "Done")
        )
      (bot/say "That role is not available citizen."))))
(defn- giveto*
  [{:keys [auth] :as client} {:keys [content] :as message}]
  (pprint message)
  (let [guild-id (get-in message [:channel :guild-id])
        role-name (-> content (clojure.string/split #"\s+") (nth 2))]
    (if-let [role (get-available-role guild-id role-name)]
      (do
        (bot-http/discord-request
          :add-user-role auth
          :guild guild-id
          :member (->> content (re-seq #"<@\d+>") first rest rest butlast (apply str))
          :role (:id role)
          )
        (bot/say "Done")
        )
      (bot/say "That role is not available citizen."))))
(comment
  (->> "Text <@581609437974757386> other text"
       (re-seq #"<@\d+>")
       first
       rest rest
       butlast
       (apply str)))

(defn- addrole*
  [{:keys [auth] :as client} {:keys [content] :as message}]
  ;(pprint client)
  ;(pprint message)
  (let [guild-id (get-in message [:channel :guild-id])
        role-name (-> content (clojure.string/split #"\s+") second)
        ]
    (if-let [role (get-role guild-id role-name)]
      (do
        (swap! available-roles conj role)
        (bot/say (str "Added role:" (:name role)))
          )
      (bot/say "lol no"))))

(defn- remrole*
  [client {:keys [content] :as message}]
  (bot/say content)
  (let [guild-id (get-in message [:channel :guild-id])
        role-name (-> content (clojure.string/split #"\s+") second)
        role (get-available-role guild-id role-name)
        ]
    (swap! available-roles (partial clojure.set/difference #{role}))))

(bot/defextension roles [client message]
  "Role management goodness"
  (:giveme
    "Gives a role."
    (giveme* client message)
    )
  (:giveto
    "Gives someone else a role"
    (giveto* client message)
    )
  (:list
    "Lists all roles"
    (list-inner client message)
    )
  (:default
    (list-inner client message)
    )
  (:addrole
    "Add a role to the list of available roles"
    (addrole* client message)
    )
  (:remrole
    "Remove a role from the list of available roles"
    (remrole* client message)
    )
  (:init
    (init-data client message)
    )
  )
