(ns meios-pagamento.domain.payments.schedule.entity.pix-payment
  [:require [meios-pagamento.util.date :as d]
            [meios-pagamento.util.util :as u]
            [clojure.tools.logging :as log]])


(defn verify-today [transaction]
  (log/info "Start today varification")
  (let [due-date (u/->instant (:due-date transaction))
        today (u/->instant (d/today))]
    (if (.isBefore today due-date)
      (throw (ex-info "Transaction above today"
                      {:transaction (-> transaction
                                        (assoc
                                          :status :due-date-fail
                                          :date-fail (u/->instant (d/today)))
                                        (update :attempt (fnil inc 1)))}))
      (assoc transaction :status :qrcode-generated))))


(defn generate-pix-qrcode [transaction]
  (log/info "Generating  pix QRCode")
  (assoc transaction :status :finished))


(def steps {:start                  verify-today
            :qrcode-generation-fail generate-pix-qrcode
            :qrcode-generated       generate-pix-qrcode
            :due-date-fail          verify-today})
;:finished               sent-to-lake!
