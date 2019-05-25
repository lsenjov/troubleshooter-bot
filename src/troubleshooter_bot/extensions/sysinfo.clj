(ns troubleshooter-bot.extensions.sysinfo
  (:require [discord.bot :as bot]
            [discord.embeds :as embeds]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre :as timbre]
            ))

(comment
  (System/getenv "OSTYPE")
  (System/getenv "NUMBER_OF_PROCESSORS")
  (System/getProperty "os.name")
  (pprint (System/getenv))
  )

(defn- sysinfo*
  [client {:keys [content] :as message}]
  (try
    (let [osinfo
          (-> (clojure.java.shell/sh "sysinfo")
              :out
              (clojure.string/split #"\|")
              (->>
                (map clojure.string/trim)
                (map vector [:header :kernel :cpu :load :mem :disk :vidya])
                (into {})))
          uptime
          (-> (clojure.java.shell/sh "uptime")
              :out
              (clojure.string/split #"\s+")
              (nth 3)
              (clojure.string/split #":")
              first
              Integer/parseInt
              (/ 24)
              (str " days")
              )
          ]
      (-> (embeds/create-embed :title "System Information")
          (embeds/+field "Operating System"
                         (-> (:header osinfo)
                             (clojure.string/split (re-pattern "\\)"))
                             last
                             clojure.string/trim
                             ))
          (embeds/+field "Kernel version" (:kernel osinfo))
          (embeds/+field "System uptime" uptime)
          (embeds/+field "RAM usage" (:mem osinfo))
          (embeds/+field "Disk usage" (:disk osinfo))
          bot/say))
  (catch Exception e (bot/say (.getMessage e)))))
(comment
  (-> "(by hxtools sysinfo) [desk] openSUSE Leap 42.3"
      (clojure.string/split (re-pattern "\\)")))
  )
(bot/defcommand sysinfo
  [client message]
  "Returns system information"
  (sysinfo* client message)
  )
