(ns meios-pagamento.util.util
  (:import (java.time Instant LocalDate ZoneId)
           (java.util Date UUID)))

(defn ->uuid [v]
  (cond
    (instance? UUID v) v
    (string? v) (UUID/fromString v)
    (nil? v) nil
    :else (throw (ex-info "Invalid UUID" {:value v}))))

(defn ->bigdec [v]
  (cond
    (instance? BigDecimal v) v
    (integer? v) (bigdec v)
    (float? v) (bigdec (str v))
    (string? v) (bigdec v)
    :else (throw (ex-info "Invalid amount" {:value v}))))

(defn ->instant
  "Aceita Date, Instant, LocalDate ou string ISO-8601 e retorna Instant."
  ([v]
   (->instant v (ZoneId/systemDefault)))
  ([v zone]
   (cond
     (nil? v) nil
     (instance? Instant v) v
     (instance? Date v) (.toInstant ^Date v)
     (instance? LocalDate v) (-> ^LocalDate v
                                 (.atStartOfDay zone)
                                 (.toInstant))
     (string? v) (Instant/parse v)
     :else (throw (ex-info "Invalid date" {:value v :class (class v)})))))



(defn instant->localdate [v]
  (cond
    (nil? v) nil
    (instance? Instant v)
    (-> ^Instant v (.atZone (ZoneId/systemDefault)) (.toLocalDate))

    (instance? Date v)
    (-> ^Date v .toInstant (.atZone (ZoneId/systemDefault)) (.toLocalDate))

    (string? v)
    (-> (Instant/parse ^String v) (.atZone (ZoneId/systemDefault)) (.toLocalDate))

    :else
    (throw (ex-info "Unsupported date type" {:value v :class (class v)}))))

