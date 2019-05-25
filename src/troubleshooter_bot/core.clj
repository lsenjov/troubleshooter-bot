(ns troubleshooter-bot.core
  (:require [discord.bot :as bot])
  )

(comment
  (bot/start)
  (bot/say "asdf")
  )

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
