(ns troubleshooter-bot.extensions.say
  (:require [discord.bot :as bot]
            [discord.http :as bot-http]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]
            ))
(defn say*
  [client {:keys [content] :as message}]
  (bot/say content)
  )

(bot/defcommand say
  [client message]
  "Bot repeats what has been said"
  (pprint client)
  (pprint message)
  (say* client message)
  )
