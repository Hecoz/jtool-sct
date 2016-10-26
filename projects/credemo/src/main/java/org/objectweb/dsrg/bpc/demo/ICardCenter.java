package org.objectweb.dsrg.bpc.demo;

import java.math.BigDecimal;
import java.util.Date;

public interface ICardCenter {
    boolean Withdraw(String CreditCardId, Date CreditCardExpirationDate, BigDecimal Amount); //throws InvalidCreditCardIdException, CreditCardExpiredException;
}
