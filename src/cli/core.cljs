(ns cli.core
  (:require
    ["yargs" :as yargs]
    [axios.core :as axios]
    [axios.transit :refer [client]]
    ["process" :as process]
    [cli.utils :refer [display-price]]
    [utils]))


(def ^:private ^js/yargs parser
  (->
    yargs
    (.scriptName "crypto-egg")
    (.command
      (clj->js ["$0 [sym]" "price [sym]"])
      "Get current price of a token in terms of USD"
      (fn [^js/yargs yargs]
        (->
          yargs
          (.positional
            "sym"
            (utils/cljkk->js
              {:default "eth"
               :default-description "Look up the current aggregate price of Ethereum to fiat USD"
               :description "Token symbol to query."
               :type "string"})))))
    (.command
      "gas [sym]"
      "get the current Ethereum fast-gas price in GWei"
      (fn [^js/yargs yargs]
        (->
          yargs
          (.positional
            "sym"
            (utils/cljkk->js
              {:default "eth"
               :default-description "Look up the current gas price of Ethereum in GWei"
               :description "Token symbol to query."
               :type "string"})))))
    (.help)))

(defn parse-args
  "Parse arguments."
  ([] (parse-args nil))
  ([ex]
   (->
     parser
     (.parse (or ex js/undefined))
     utils/js->cljkk)))

(comment
  (parse-args) ; {:_ [], :help true, :sym "eth", :$0 "crypto-egg"}
  (parse-args "--help") ; {:_ [], :help true, :sym "eth", :$0 "crypto-egg"}
  (parse-args "eth") ; {:_ [], :sym "eth", :$0 "crypto-egg"}
  (parse-args "btc") ; {:_ [], :sym "btc", :$0 "crypto-egg"}
  (parse-args "price") ; {:_ ["price"], :sym "eth", :$0 "crypto-egg"}
  (parse-args "price btc") ; {:_ ["price"], :sym "btc", :$0 "crypto-egg"}
  (parse-args "gas polygon") ; {:_ ["gas"], :sym "polygon", :$0 "crypto-egg"}
  (parse-args "-q '[{[:coin/sym \"btc\"] [:coin/price :coin/ath]}]'"))

(defn make-get-price [sym]
  (->
    client
    (axios/post
      "http://localhost:3000/api"
      [{[:coin/sym sym] [:coin/price]}])
    (.then (comp :coin/price first vals))
    (.then #(if (nil? %) "N/A" (display-price %)))
    (.then print)
    (.catch
      (fn [err]
        (let [response (-> err
                         (.-response)
                         (utils/js->cljkk))
              status (:status response)]
          (when (= status 404)
            (print "N/A")))))))

(comment
  (make-get-price "btc")
  (make-get-price "zoo"))

(defn ^:export main
  "Query information based on token symbol."
  []
  (let [args (parse-args)
        command (first (:_ args)) ; {:_ [command]}
        sym (:sym args)]
    (condp = command
      "price" (make-get-price sym)
      (-> (.getHelp parser) (.then print)))))

(comment
  (set! (.. process -argv)
        #js
         ["/home/berkeleytrue/dvlpmnt/crypto/crypto-egg/bin/crypto-egg"
          "price"
          "xtz"])
  (print (.-argv process)) ; #js ["/usr/local/bin/node"
                           ; "/home/berkeleytrue/dvlpmnt/crypto/crypto-egg/bin/crypto-egg"]
  (main))
