(ns meios-pagamento.domain.payments.schedule.use-case.schedule.schedule-payment-use-case
  (:require [meios-pagamento.domain.payments.schedule.entity.schedule-payment :as schedule]
            [meios-pagamento.domain.payments.schedule.entity.pix-payment :as pix-payment]
            [meios-pagamento.util.date :as date]))



(defn- verify-date [schedule-date]
  (if (not (schedule/schedule-future? schedule-date))
    (throw (ex-info "Transaction above today"
                    {:code          :actual-date-above-today
                     :today         date/today
                     :schedule-date schedule-date}))))
;(pix-payment/generate-pix-qrcode)

;(defn- chose-payment-method [transaction]
;  (let [type (:type transaction)]
;    (cond (= :pix type) [verify-date _])))

(defn- save-schedule [transaction]
  (print "Schedule save" transaction))


(defn schedule [transaction]
  (let [schedule-date (:date transaction)]
    (verify-date schedule-date)
    (save-schedule transaction)))


