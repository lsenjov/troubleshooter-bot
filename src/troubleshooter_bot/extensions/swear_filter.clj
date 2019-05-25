(ns troubleshooter-bot.extensions.swear-filter
  (:require [discord.bot :as bot]
            [discord.http :as bot-http]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]
            ))

(comment
  (->> (ns-publics 'discord.http)
       keys
       pprint
       )
  (bot-http/edit-message)
  )

(def swears
  #{"commie" "treason"}
  )

(def swear-capture-pattern
  (->> swears
     (map #(str "((?i)" % \)))
     (interpose \|)
     (apply str)
     (#(str "(" % ")"))
     re-pattern
     ))
(defn replacement-string
  [s]
  (apply str (first s) (repeatedly (dec (count s))
                                   #(rand-nth "!@#$%^&*")))
  )
(defn- filter-string
  [s]
  (if-let [item (first (map first (re-seq swear-capture-pattern s)))]
    (recur
      (clojure.string/replace
        s
        item
        (replacement-string item)
        ))
    s
    ))
(comment
  (filter-string "fine fucking work commie treason traitor")
  (replacement-string "commie")
  (re-seq swear-capture-pattern "fuck shit cunt")
  (re-seq swear-capture-pattern "all fine here")
         )
(defn swear-filter-handler*
  [prefix client {:keys [content id] :as message}]
  (timbre/debug \newline
                "    " prefix \newline
                "    " (pprint client) \newline
                "    " (pprint message) \newline
                "    " content
                )
  (if (re-seq swear-capture-pattern content)
    (do
      (bot-http/delete-message
        (get-in client [:auth])
        (get-in message [:channel :id])
        id
        )
      (bot/say (str "**" (get-in message [:author :username]) "**: "
                    (filter-string content)
                    ))
      )))
(bot/defhandler swear-filter-handler
  [prefix client message]
  (swear-filter-handler* prefix client message)
  )
