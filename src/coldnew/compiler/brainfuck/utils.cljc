(ns coldnew.compiler.brainfuck.utils
  (:require [clojure.string :as str]))

(defn- build-indent
  "Generate intent string according to indent-depth in ir."
  [ir]
  (let [depth (:indent-depth ir)
        indent-depth (if (= depth 0) 1 depth)]
    (apply str (repeat indent-depth "\t"))))

(defn line-indent
  "Generate string prepend with indent-depth and append with newline char."
  [ir & args]
  (let [indent (build-indent ir)]
    (str indent (reduce str args) "\n")))
