(ns server.config
  (:require [integrant.core :as ig]))

(def env
  {:system/http
   {:port 3000
    :host "127.0.0.1"
    :handler (ig/ref :router/handler)}

   :db/conn
   {:schema
    {:coin
     {:db/cardinality :db.cardinality/many}
     :coin/id
     {:db/valueType :db.type/ref
      :db/unique :db.unique/identity
      :db/cardinality :db.cardinality/one
      :db/doc "coingecko identifier"}
     :coin/sym
     {:db/valueType :db.type/ref
      :db/unique :db.unique/identity
      :db/cardinality :db.cardinality/one
      :db/doc "coingecko identifier"}
     :coin/ath {}
     :coin/price {}}}

   :router/handler {}
   :service/coingecko
   {:coins
    ; must use coin gecko id's
    ["bitcoin"
     "ethereum"
     "tezos"
     "pickle-finance"
     "olympus"
     "ethereum-name-service"]}})
