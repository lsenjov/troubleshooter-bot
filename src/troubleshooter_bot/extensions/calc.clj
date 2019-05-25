(ns troubleshooter-bot.extensions.calc
  (:require [discord.bot :as bot]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]
            )
  )

(comment
  (bot/clear-extensions!)
  (->> (ns-publics 'discord.bot) vals pprint)
  )

(def operators
  {"+" +
   "-" -
   "*" *
   "/" /
   }
  )

(defn- calc-inner
  "Calculate an artibrary but odd number of items"
  [f op s & more]
  (timbre/debugf "f:" f "op:" op "s:" s "more:" more "count-more:" (count more))
  (let [f (if (string? f) (Float/parseFloat f) f)
        s (if (string? s) (Float/parseFloat s) s)
        op (if (get operators op) (get operators op) (throw (Exception. (format "Operator not found: '%s'" op))))
        ]
    (if (= 0 (count more))
      (op f s)
      (apply calc-inner (op f s) more)
      ))
  )
(comment
  (calc-inner "3" "+" "4" "+" "5")
  (.getMessage (Exception. "Operator not found"))
  )
(defn- calc*
  [client {:keys [content] :as message}]
  (try
    (let [tokens (-> content (clojure.string/split #"\s+"))]
      (->> tokens
          (apply calc-inner)
          pr-str
          bot/say
          ))
    (catch Exception e
      (bot/say (.getMessage e))
      )))
(comment
  (clojure.string/split "a b   c" #"\s+")
  )

(bot/defcommand calc
  [client message]
  "Calculates a list of binary operators, left to right"
  (calc* client message)
  )
