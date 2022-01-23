(ns server.core
  (:require
    [taoensso.timbre :as timbre :refer-macros [info]]
    [integrant.core :as ig]

    [com.wsscode.pathom3.connect.operation :as pco :refer-macros [defresolver]]
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom3.interface.eql :as peql]
    [com.wsscode.pathom.viz.ws-connector.core :as pvc]
    [com.wsscode.pathom.viz.ws-connector.pathom3 :as p.connector]

    [server.infra]
    [server.app]
    [server.config :refer [env]]
    [server.services.coingecko.core]))


(defresolver coin-price [{:coin/keys [sym]}]
  {::pco/output [:coin/price]}
  (info sym)
  {:coin/price 91.38})

(def pathom-env
  (->
    (pci/register [coin-price])
    (p.connector/connect-env {::pvc/parser-id `pathom-env})))

(comment (peql/process pathom-env [{[:coin/sym "btc"] [:coin/price]}]))


(defonce system-ref (atom nil))

(defn ^:dev/after-load main []
  (info "hot load")
  (reset! system-ref (ig/init env)))

(defn ^:dev/before-load stop []
  (info "cooldown")
  (when-some [system @system-ref]
    (ig/halt! system)
    (reset! system-ref nil)))
