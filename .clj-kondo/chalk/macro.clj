(ns chalk.macro)

(defmacro gen-methods [_ & methods]
  (list* 'do
         (map
           (fn [method#]
             `(defn ~(symbol (name method#)) [#^String string]))
           methods)))
