(ns coldnew.compiler.brainfuck.ir
  (:require [clojure.pprint :refer [cl-format]]
            [clojure.string :as str]))

(declare to-ir)

(defn ast->ir
  [ast]
  (map to-ir ast))

(defn to-ir
  ([cmd] (to-ir cmd 1))
  ([cmd indent-depth]
   (case cmd
     \+  {:op :add
          :indent-depth indent-depth
          :form (str cmd)
          :comment "Increment (increase by one) the byte at the data pointer."}
     \-  {:op :sub
          :indent-depth indent-depth
          :form (str cmd)
          :comment "Decrement (decrease by one) the byte at the data pointer."}
     \>  {:op :right
          :indent-depth indent-depth
          :form (str cmd)
          :comment "Increment the data pointer (to point to the next cell to the right)."}
     \<  {:op :left
          :indent-depth indent-depth
          :form (str cmd)
          :comment "Decrement the data pointer (to point to the next cell to the left)."}
     \. {:op :output
         :indent-depth indent-depth
         :form (str cmd)
         :comment "Output the byte at the data pointer."}
     \, {:op :input
         :indent-depth indent-depth
         :form (str cmd)
         :comment "Accept one byte of input, storing its value in the byte at the data pointer."}
     (if (vector? cmd)
       {:op :loop
        :indent-depth indent-depth
        :form (str/replace (cl-format nil "[~d]" (apply str cmd)) #"[\\\s]" "")
        :children (map #(to-ir % (inc indent-depth)) cmd)
        :comment "Loop until data pointer is 0"}
       (throw (ex-info "Unknown cmd to generate IR." {:op cmd}))))))