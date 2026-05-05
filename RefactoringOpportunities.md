# Refactoring Opportunities

| Local                           | Code Smell | Refactoring | Nº Aluno |
|---------------------------------|------------|-------------|----------|
| Tasks::menu                     | Long Method | Extract Method | 122640 |
| Tasks::readClassicPosition      | Complex Method | Extract Method | 122640 |
| Position::adjacentPositions     | Long Method | Extract Method | 122640 |
| Game::randomEnemyFire           | Complex Method | Simplify Conditional / Extract Method | 122640 |
| Fleet::addShip                  | Long Parameter List | Introduce Parameter Object | 122640 |
| Tasks::(Thread.sleep loop)      | Busy Waiting | Replace with event-based logic | 122640 |
| Tasks::scoreboard               | Data Class | Encapsulate Field / Make final | 122640 |
| Tasks::(condition always false) | Dead Code | Remove Conditional | 122640 |
| Tasks::readShip                 | Complex Method | Extract Method | 122648 |
| Game::fireSingleShot            | Complex Conditional | Simplify Conditional | 122648 |
| Fleet::createRandom             | Long Method | Extract Method | 122648 |
| Fleet::colisionRisk             | Imperative Loop | Replace with Stream | 122648 |
| Fleet::printStatus              | Dead Code | Remove Dead Code | 122648 |
