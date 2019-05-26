(ns troubleshooter-bot.extensions.calc
  (:require [discord.bot :as bot]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]
            )
  )


(def operators
  {"+" +
   "-" -
   "*" *
   "/" /
   "^" #(Math/pow %1 %2)
   "%" mod
   }
  )

(defn- parse-float-or-throw
  [f]
  (try (if (string? f) (Float/parseFloat f) f)
       ; Transform from the previous message to something nicer for outputting
       (catch Exception e (throw (Exception. (format "Could not parse token: '%s'" f))))
       ))
(defn- calc-inner
  "Calculate an artibrary but odd number of items."
  ([f]
   (parse-float-or-throw f)
   )
  ([f op s & more]
  (timbre/debug "f:" f "op:" op "s:" s "more:" more "count-more:" (count more))
  (let [f (parse-float-or-throw f)
        s (parse-float-or-throw s)
        op (if (get operators op) (get operators op) (throw (Exception. (format "Operator not found: '%s'" op))))
        ]
    (if (= 0 (count more))
      (op f s)
      (apply calc-inner (op f s) more)
      )))
  )
(comment
  (calc-inner "3" "+" "4" "+" "5")
  (.getMessage (Exception. "Operator not found"))
  )
(defn- calc*
  [client {:keys [content] :as message}]
  (try
    (let [tokens (-> content (clojure.string/split #"\s+"))]
      (if (even? (count tokens)) (throw (Exception. "Invalid number of arguments")))
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
