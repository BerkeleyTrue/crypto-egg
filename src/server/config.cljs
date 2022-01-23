(ns server.config
  (:require [integrant.core :as ig]))

(def env
  {:infra.macchiato/http
   {:port 3000
    :host "127.0.0.1"
    :handler (ig/ref :router/handler)}

   :infra.datascript/conn
   {:schema
    {:coin
     {:db/cardinality :db.cardinality/many}
     :coin/id
     {:db/unique :db.unique/identity
      :db/cardinality :db.cardinality/one
      :db/doc "coingecko identifier"}
     :coin/sym
     {:db/unique :db.unique/identity
      :db/cardinality :db.cardinality/one
      :db/doc "coingecko identifier"}
     :coin/ath {}
     :coin/price {}}}

   :router/handler {}

   :app.service/coingecko
   {:coins
    ; must use coin gecko id's
    ["bitcoin"
     "ethereum"
     "tezos"
     "pickle-finance"
     "olympus"
     "ethereum-name-service"]
    :conn (ig/ref :infra.datascript/conn)}})
