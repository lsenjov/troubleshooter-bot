(ns troubleshooter-bot.extensions.whatshot
  (:require [discord.bot :as bot]
            [discord.http :as bot-http]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]

            [troubleshooter-bot.sql :as sql]
            ))

(def ignore-words
  #{
    "roles" "blinky" "addrole" "giveto" "help" "pac_ghost" "whatshot" "sysinfo"
    "what"
    })

(defn word-remover
  "Return the word if valid, else falsey"
  [word]
  (if
    ; Conditions for _removing_ a word
    (or
      (<= (count word) 3)
      (ignore-words (clojure.string/lower-case word)))
    nil
    word
    ))
(comment
  (word-remover "the")
  (word-remover "asdf"))

(defn whatshot-handler*
  [prefix client {:keys [content id] :as message}]
  (let [tokens (->> content
                    (re-seq #"[A-Za-z']+")
                    (filter word-remover)
                    )]
    (timbre/debug \newline
                  ;"    " prefix \newline
                  ;"    " (pprint client) \newline
                  "    " (get-in message [:author :username]) \newline
                  "    " (vec tokens))
    (->> tokens
         (map (partial hash-map :word))
         (sql/insert-multi! :wordtrack))))
(bot/defhandler whatshot-handler
  [prefix client message]
  (whatshot-handler* prefix client message))

; Now for the command

(defn- whatshot-inner
  ([num-words]
   (->> (if (empty? num-words) "3" num-words)
        Integer/parseInt
        (assoc {} :num)
        sql/get-words
        ; Make it a nice readable format
        (map (fn [{:keys [word wc]}]
               (format "%4d: %s" wc word)))
        (interpose \newline)
        (apply str))))
(comment
  (whatshot-inner "5"))
(defn- whatshot*
  [client {:keys [content] :as message}]
  (try
    (bot/say (whatshot-inner content))
    (catch Exception e
      (bot/say (.getMessage e)))))

(bot/defcommand whatshot
  [client message]
  "Displays the most said words from the last 24 hours"
  (whatshot* client message))
