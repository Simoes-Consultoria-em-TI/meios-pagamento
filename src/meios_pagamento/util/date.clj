(ns meios-pagamento.util.date
  (:import [java.time LocalDate]))

(defn today []
  (LocalDate/now))
