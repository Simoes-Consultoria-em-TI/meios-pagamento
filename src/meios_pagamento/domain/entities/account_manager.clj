(ns meios-pagamento.domain.entities.account-manager)


(defn withdraw? [account]
  (let [status (:status account)]
    (cond (= :active status) true
                    :else false)))

(def withdraw-minimal-values-by-method
  {:atm 20
   :pix 1
   :transfer-same-owner 1
   :transfer-others 1
   :taller 10})

(def withdraw-max-values-by-method
  {:atm 1000
   :pix 5000
   :transfer-same-owner 15000
   :transfer-others 1000
   :taller 2000})

(defn withdraw-account [account transaction]
  (let [account-balance (:account-balance account)
        amount (:amount transaction)
        actual-balance (- account-balance amount)]
    (when (neg? account-balance)
      (throw (ex-info
               "Insuficiente balance"
               {:type      :business-error
                :code      :insufficient-funds
                :balance   account-balance
                :attempted amount})))
    actual-balance))





