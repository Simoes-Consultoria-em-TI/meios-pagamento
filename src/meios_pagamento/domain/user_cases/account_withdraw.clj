(ns meios-pagamento.domain.user_cases.account-withdraw
  (:require [meios-pagamento.domain.entities.account-manager :as am]))


(defn- verify-minimal-value [transaction]
  (let [type (:type transaction)
        amount (:amount transaction)
        minimal-value (type am/withdraw-minimal-values-by-method)]
    (when (< amount minimal-value)
      (throw (ex-info "Transaction below minimal value"
                      {:code :minimal-value-not-met
                       :type type
                       :amount amount
                       :minimal minimal-value})))))

(defn- verify-max-value [transaction]
  (let [type (:type transaction)
        amount (:amount transaction)
        max-value (type am/withdraw-max-values-by-method)]
    (when (> amount max-value)
      (throw (ex-info "Transaction above max value"
                      {:code    :max-value-not-met
                       :type    type
                       :amount  amount
                       :minimal max-value})))))

(defn- verify-withdraw-account-enabled [account]
  (if (not (am/withdraw? account))
    (throw (ex-info "Withdraw not possible"
                    {:code :withdraw-not-possible
                     :account account}))))


(defn withdraw [account transaction]
  (verify-withdraw-account-enabled account)
  (verify-minimal-value transaction)
  (verify-max-value transaction)
  (assoc account :account-balance (am/withdraw-account account transaction)))


(withdraw
  {:account-balance 20000M :status :active}
  {:type :pix :amount 1000M})








