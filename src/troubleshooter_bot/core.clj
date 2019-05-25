(ns troubleshooter-bot.core
  (:require [discord.bot :as bot]
            [clojure.pprint :refer [pprint]]
            )
  )

(comment
  (bot/start)
  (bot/say "asdf")
  (bot/clear-extensions!)
  (bot/register-builtins!)
  (->> (ns-publics 'discord.bot) vals pprint)
  (->> (bot/get-extensions) pprint)
  )

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
