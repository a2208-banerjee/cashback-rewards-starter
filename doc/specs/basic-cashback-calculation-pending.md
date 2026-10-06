# Basic Cashback Calculation: Example Map

**Story:** As a customer, I want to earn cashback on my purchases so that I'm rewarded for shopping with partner merchants.

## Rule: Must calculate cashback as the merchant's rate × purchase amount, rounded half-up to 2 decimals

| Purchase amount | Merchant rate | Cashback | Note |
|---|---|---|---|
| 100.00 | 2% | 2.00 | exact cent, no rounding |
| 200.00 | 1.5% | 3.00 | fractional rate |
| 33.33 | 5% | 1.67 | raw 1.6665 rounds up |
| 0.10 | 5% | 0.01 | raw 0.005 is a tie and rounds up |
| 0.09 | 5% | 0.00 | raw 0.0045 rounds down |

- **Counter-example:** The one where a merchant has a 0% rate. That is a valid configuration and earns 0.00.

## Rule: Must use the rate configured for the merchant where the purchase happened

- **Example:** The one where the same customer spends 100.00 at a 2% merchant and 100.00 at a 5% merchant. They earn 2.00 and 5.00.

## Rule: Must apply the rate in force at the time of purchase

- **Example:** The one where a merchant changes its rate from 2% to 3% after a customer's purchase. The earlier purchase keeps its 2% cashback, and later purchases earn 3%.

## Rule: Must reject purchases at merchants that are not partners

- **Example:** The one where a purchase is submitted for a merchant with no configured rate. It is rejected with a domain error (404) and nothing is recorded.

## Rule: Must record newly earned cashback as pending, shown separately from the available balance

- **Example:** The one where a customer with a 10.00 available balance earns 2.00. Available stays 10.00 and pending becomes 2.00.
- **Counter-example:** The one where a customer with no rewards balance makes a first purchase. A balance is created with 0.00 available and 2.00 pending.

## Rule: Must move pending cashback to the available balance when the merchant confirms the purchase

- **Example:** The one where the merchant confirms a purchase that earned 2.00. Pending drops by 2.00 and available rises by 2.00.
- **Counter-example:** The one where a purchase is not yet confirmed. Its cashback stays pending and the available balance is unchanged.

## Rule: Must treat a negative purchase amount as a refund, reducing pending cashback first and then the available balance

- **Example:** The one where a customer earned 2.00 on 100.00 at 2%, still pending, then a refund of -100.00 is processed. Pending drops by 2.00 and the available balance is untouched.
- **Example:** The one where the same purchase was already confirmed before a -100.00 refund. The 2.00 comes off the available balance.
- **Counter-example:** The one where a refund exceeds the pending and available amounts combined. The available balance is allowed to go negative. For example, 0.50 available and 0.00 pending refunded by 2.00 leaves -1.50 available.

## Notes

- **Rounding is half-up.** `CLAUDE.md` and `README.md` were updated from `RoundingMode.DOWN` to `RoundingMode.HALF_UP`.
- **Confirmation is a new capability.** The merchant confirming a purchase needs its own entry point, such as `POST /purchases/{id}/confirmation`. It also means purchases need identifiers.
- **Refunds use the current rate.** A refund is computed from the rate in force when the refund is processed. It is not linked to the original purchase. Matching a refund to its purchase is a separate story.
