(ns meios-pagamento.util.loggin
  (:import [org.slf4j MDC]
           [java.util UUID]))

(defn generate-correlation-id []
  (str (UUID/randomUUID)))

(defn set-correlation-id! [id]
  (MDC/put "correlation-id" id))

(defn clear-correlation-id! []
  (MDC/remove "correlation-id"))

