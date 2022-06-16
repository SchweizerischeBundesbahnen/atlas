# Stages and their usage

| Development           | Test                      | Integration                                          | Production          |
|-----------------------|---------------------------|------------------------------------------------------|---------------------|
| Developers Playground | E2E Tests while staging   | **Production-near data**                             | **Production data** |
| POCs and more         | Extensive Testing by Joel | Periodic copy of production data                     |                     |
|                       |                           | API users and external customers (Liip, Info+, etc.) |                     |
|                       |                           | Business demos and trainings                         |                     |
|                       |                           | Nightly Release and E2E Tests                        |                     |