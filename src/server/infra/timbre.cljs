(ns server.infra.timbre
  (:require
    [taoensso.timbre :as timbre]
    [chalk.core :as chalk]))

(def level-to-color
  {:debug chalk/cyan
   :info chalk/blue
   :warn chalk/yellow
   :error chalk/red
   :fatal chalk/bg-red})

(defn timbre-output-wrap
  ([data] (timbre-output-wrap nil data))
  ([opts data]
   (let [level (:level data)
         color-fn (get level-to-color level chalk/white)
         res (timbre/default-output-fn opts data)]
     (color-fn res))))

(timbre/merge-config!
  {:timestamp-opts {:pattern "HH:MM:SS"}
   :output-fn timbre-output-wrap})

(comment
  (timbre/debug "debug")
  (timbre/info "info")
  (timbre/warn "warn")
  (timbre/error "error")
  (timbre/fatal "fatal"))
