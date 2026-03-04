(ns meios-pagamento.domain.payments.schedule.entity.schedule-payment
  (:require [meios-pagamento.util.date :as date]))


(defn schedule-future? [schedule-date]
  (if (< schedule-date date/today)
    true
    false))




