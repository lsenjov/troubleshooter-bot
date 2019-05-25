(ns troubleshooter-bot.sql
  (:require [discord.bot :as bot]
            [discord.http :as bot-http]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]
            [hugsql.core :as hugsql]
            [clojure.java.jdbc :as jdbc]
            ))

(def db-spec
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "file"
   :dbtype "sqlite"
   :dbname "tsbot.db"
   }
  )

(def insert!
  "[table record]
  Inserts a single row into the table"
  (partial jdbc/insert! db-spec))
(def insert-multi!
  "[table rows]
  Inserts many rows into the table"
  (partial jdbc/insert-multi! db-spec))

