(ns web3.core
  (:require
    ["web3" :as web3js]))


(defn http-provider [uri]
  (let [HttpProvider (.. web3js -providers -HttpProvider)]
    (new HttpProvider uri)))

(defn ipc-provider [path]
  (let [IpcProvider (.. web3js -providers -IpcProvider)]
    (new IpcProvider path)))

(defn create
  "Create a web3 instance"
  [x]
  (new web3js x))
