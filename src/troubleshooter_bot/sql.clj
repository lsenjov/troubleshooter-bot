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
(comment
  (jdbc/execute! db-spec "create table if not exists test(id integer auto_increment not null);")
  (map println  (jdbc/query db-spec "select * from notes order by id limit 3;"))
  (map println  (jdbc/query db-spec "select * from activities order by id limit 3;")))

; Initialise wordtrack db on start if it doesn't exist
(hugsql/def-db-fns "sql/init.sql")
(create-wordtrack-table db-spec)

(def insert!
  "[table record]
  Inserts a single row into the table"
  (partial jdbc/insert! db-spec))
(def insert-multi!
  "[table rows]
  Inserts many rows into the table"
  (partial jdbc/insert-multi! db-spec))

(hugsql/def-db-fns "sql/wordtrack.sql")
(get-words* db-spec {:num 5})
(def get-words (partial get-words* db-spec))
