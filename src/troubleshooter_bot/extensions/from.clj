(ns troubleshooter-bot.extensions.from
  (:require [discord.bot :as bot]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]
            )
  )


(defn- from*
  [client {:keys [content] :as message}]
  (try
  (let [[from-val from-type _ to-type :as tokens] (-> content (clojure.string/split #"\s+"))
        _ (if (not (= 4 (count tokens))) (throw (Exception. "Invalid input string. `!help from`")))
        ; Abuse units to do the heavy lifting
        ret (clojure.java.shell/sh "units" (str from-val from-type) to-type)
        ]
    (timbre/debug ret)
    (if (zero? (:exit ret))
      (-> ret
          :out
          (clojure.string/split #"\n")
          first
          (clojure.string/split #"\s")
          last
          (str " " to-type)
          bot/say
          )
      (-> ret
          :out
          (clojure.string/split #"\n")
          first
          (str "\n`help from` for more details")
          bot/say
          )))
  (catch Exception e (bot/say (.getMessage e)))))
(comment
  (clojure.java.shell/sh "units" "29.1cm" "oz")
  )
(bot/defcommand from
  [client message]
  "Converts units from one to another.
  Format example: from 10 cm to m"
  (from* client message)
  )
