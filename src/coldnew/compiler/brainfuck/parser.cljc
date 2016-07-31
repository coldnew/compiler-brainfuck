(ns coldnew.compiler.brainfuck.parser)

(defn remove-comments
  "Remove all comments in brainfuck sources."
  [code]
  (apply str (filter (into #{} "<>-+,.[]") code)))

(defn tokenize
  [code]
  (seq code))

(defn parse-loop [token-seq]
  (loop [ast []
         token-seq token-seq]
    (cond (empty? token-seq)       (throw (ex-info "Unmatched [ found " {:code token-seq}))
          (= (first token-seq) \[) (let [[ast-b token-seq-b] (parse-loop (rest token-seq))]
                                     (recur (conj ast ast-b) token-seq-b))
          (= (first token-seq) \]) [ast (rest token-seq)]
          :else
          (recur (conj ast (first token-seq)) (rest token-seq)))))

(defn parse
  "Parses a given code in string format."
  [code]
  (loop [ast []
         token-seq (-> code remove-comments tokenize)]
    (if (empty? token-seq)
      ast
      (case (first token-seq)
        \[ (let [[ast-b token-seq-b] (parse-loop (rest token-seq))]
             (recur (conj ast ast-b) token-seq-b))
        \] (throw (ex-info "Unmatched ] found " {:code token-seq}))
        ;; default
        (recur (conj ast (first token-seq)) (rest token-seq))))))
