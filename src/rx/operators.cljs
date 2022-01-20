(ns rx.operators
  (:refer-clojure
    :exclude
    [true? map filter reduce merge repeat first
     last mapcat repeatedly zip dedupe drop
     take take-while map-indexed concat empty
     delay range throw do trampoline subs flatten])
  (:require
    ["rxjs/operators" :as op]))

(defn filter [predicate]
  (.filter op #(boolean (predicate %))))

(defn map [project]
  (.map op project))

(defn switch-map [project]
  (.switchMap op project))

(defn catch-error [selector]
  (.catchError op selector))

(defn ignore-elements
  "Ignores all items emitted by the source Observableand
  and only passes calls of complete or error."
  []
  (js-invoke op "ignoreElements"))

(def ^:private -tap (.-tap op))

(defn tap
  "Used to perform side-effects for notifications from the source observable"
  ([] (-tap))
  ([observerOrNext] (-tap observerOrNext))
  ([next error] (-tap next error))
  ([next error complete] (-tap next error complete)))
