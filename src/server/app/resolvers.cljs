(ns server.app.resolvers
  (:require
    [com.wsscode.pathom3.connect.operation :as pco :refer-macros [defresolver]]
    [datascript.core :as d]))


(defresolver coin-price [{:datascript/keys [conn]} {:coin/keys [sym]}]
  {::pco/output [:coin/name
                 :coin/price
                 :coin/ath
                 :coin/sym]}

  (let [results (d/q '[:find ?name ?price ?ath ?sym
                       :in $ ?sym
                       :where
                       [?e :coin/sym ?sym]
                       [?e :coin/price ?price]
                       [?e :coin/id ?name]
                       [?e :coin/ath ?ath]]

                     @conn
                     sym)
        res (first results)
        coin-name (first res)
        price (second res)
        ath (nth res 2)]

    {:coin/price price
     :coin/name coin-name
     :coin/ath ath
     :coin/sym sym}))
