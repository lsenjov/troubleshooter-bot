(ns troubleshooter-bot.extensions.whatshot
  (:require [discord.bot :as bot]
            [discord.http :as bot-http]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]

            [troubleshooter-bot.sql :as sql]
            ))
