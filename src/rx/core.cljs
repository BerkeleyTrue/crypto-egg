(ns rx.core
  (:require
    ["rxjs" :as rxjs]))

(defn pipe
  ([] (fn [input] input))
  ([op] op)
  ([op & ops]
   (fn [input]
     (reduce
       (fn [prev fn] (fn prev))
       input
       (into [op] ops)))))

(defn defer [factory]
  (.defer rxjs factory))

(defn of [& args]
  (apply js-invoke rxjs "of" args))
