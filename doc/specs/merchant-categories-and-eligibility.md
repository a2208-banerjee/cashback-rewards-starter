# Merchant Categories & Eligibility: Example Map

**Story:** As the card rewards product manager, I want cashback rates to vary by merchant category and only apply to eligible transactions, so that rewards steer spend to the right places and we don't pay for things that shouldn't earn.

## Rule: Must calculate cashback using the rate of the merchant's category, derived from its merchant category code (MCC)

| MCC | Category | Rate | Purchase of 100.00 earns |
|---|---|---|---|
| 5411, 5422, 5441, 5451, 5462 | Groceries | 2% | 2.00 |
| 5541, 5542 | Fuel | 1% | 1.00 |
| any other code, e.g. 5812 | Default | 0.5% | 0.50 |

- **Counter-example:** The one where a transaction carries no MCC, or one we don't recognise. It earns the Default 0.5%. It is not rejected and does not earn 0%.

## Rule: Must only earn cashback on transactions that have posted

- **Example:** The one where a purchase is still pending. It earns nothing, and no cashback record exists for it yet.
- **Counter-example:** The one where that purchase later posts. It now earns cashback, judged on the card status and category rates in force at the moment it posts, not at purchase time.
- **Counter-example:** The one where a pending purchase is cancelled or reversed before it posts. It is recorded as dropped and earns nothing, so there is nothing to claw back.

## Rule: Must only earn cashback on active cards

| Card status when the transaction posts | Earns cashback |
|---|---|
| Active | Yes |
| Frozen | No |
| Cancelled | No |

- **Counter-example:** The one where a card is frozen and later unfrozen. A purchase that posted while it was frozen is never credited retroactively. A purchase that was pending while frozen and posts after the unfreeze earns normally, because status is judged at posting.

## Rule: Must only earn cashback on purchases

| Transaction type | Earns new cashback |
|---|---|
| Purchase | Yes |
| Refund | No |
| Fee | No |
| Cash advance | No |
| Balance transfer | No |
| Interest charge | No |

- **Counter-example:** The one where a refund is processed. It earns no new cashback, but it still claws back the cashback earned on the original purchase, as in the existing refund rule.

## Rule: Must record an ineligible transaction normally but create no cashback record for it

- **Example:** The one where a Groceries purchase of 100.00 posts on a frozen card. The transaction is accepted and recorded as usual, but there is no cashback record and the rewards balance is unchanged.
- **Counter-example:** The one where a purchase passes only some of the conditions, for example it is posted and a purchase but the card is cancelled. It still earns nothing. All three eligibility conditions (posted, active card, purchase) must hold.

## Notes

- **Category replaces the per-merchant rate.** Rates come only from the category, via the MCC. The existing per-merchant rate and its change endpoint are retired. This supersedes "Must use the rate configured for the merchant" and reshapes "Must apply the rate in force at the time of purchase" into a category rate fixed at posting time.
- **Eligibility is judged at posting time.** Card status and category rates are evaluated when the transaction posts.
- **Wider grocery set.** Groceries covers grocery stores, meat, candy, dairy and bakeries. Fuel covers 5541 and 5542.
- **No cashback record for ineligible transactions.** The monthly report only lists cashback events, so ineligible transactions don't appear there, consistent with the existing gift-card example. The cost is that customers and support can't see why a transaction earned nothing.
- **Dropped pending transactions** leave a transaction-status record, not a cashback record. That doesn't contradict the "no cashback record" rule.
- **Rounding is unchanged:** half-up to 2 decimals.
