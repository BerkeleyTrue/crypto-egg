(ns server.app.resolvers
  (:require
    [taoensso.timbre :as log]
    [com.wsscode.pathom3.connect.operation :as pco :refer-macros [defresolver]]))

(defresolver coin-price [{:coin/keys [sym]}]
  {::pco/output [:coin/price]}
  (log/info sym)
  {:coin/price 91.38})
