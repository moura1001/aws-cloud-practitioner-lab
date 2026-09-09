# 02 — AWS Global Infrastructure & Well-Architected Framework

## AWS Global Infrastructure

A infraestrutura global da AWS é formada por diferentes componentes distribuídos geograficamente, permitindo construir aplicações com diferentes níveis de disponibilidade, resiliência e desempenho.

Os principais conceitos estudados neste módulo são:

* AWS Regions
* Availability Zones
* Edge Locations
* AWS Well-Architected Framework

---

## AWS Region

Uma **AWS Region** é uma área geográfica onde a AWS possui infraestrutura de Data Centers.

Uma Region possui múltiplas Availability Zones independentes.

Exemplo:

```text
AWS Region
├── Availability Zone A
├── Availability Zone B
└── Availability Zone C
```

A escolha de uma Region pode considerar fatores como:

* Latência para os usuários;
* Disponibilidade dos serviços;
* Requisitos de conformidade;
* Localização dos dados;
* Custos.

**Importante:** uma Region não deve ser definida simplesmente como um país. Ela representa uma **área geográfica da AWS**.

---

## Availability Zone

Uma **Availability Zone (AZ)** é uma localização isolada dentro de uma AWS Region.

As AZs são projetadas com isolamento de infraestrutura para reduzir o impacto de falhas.

Uma aplicação pode distribuir seus recursos entre diferentes AZs para aumentar sua disponibilidade e resiliência.

Exemplo:

```text
Region
├── AZ A
│   └── EC2
├── AZ B
│   └── EC2
└── AZ C
    └── EC2
```

Uma Availability Zone pertence a uma única Region.

---

## Edge Location

Uma **Edge Location** é um ponto de presença da infraestrutura da AWS localizado próximo aos usuários.

Um dos principais serviços associados às Edge Locations é o **Amazon CloudFront**, que utiliza essa infraestrutura para entregar conteúdo com menor latência.

Exemplo:

```text
Usuário
   ↓
Edge Location
   ↓
CloudFront
   ↓
Origem
```

**Importante:** Edge Location não significa apenas "cache". É um ponto de presença da infraestrutura da AWS, sendo o cache de conteúdo um dos usos do CloudFront.

---

# AWS Well-Architected Framework

O **AWS Well-Architected Framework** fornece uma estrutura para avaliar e melhorar arquiteturas de aplicações na AWS.

Ele é organizado em seis pilares:

| Pilar                  | Principal objetivo                              |
| ---------------------- | ----------------------------------------------- |
| Operational Excellence | Operar e melhorar continuamente                 |
| Security               | Proteger sistemas e dados                       |
| Reliability            | Recuperar-se de falhas e manter o funcionamento |
| Performance Efficiency | Utilizar recursos de forma eficiente            |
| Cost Optimization      | Evitar custos desnecessários                    |
| Sustainability         | Reduzir impactos ambientais                     |

---

## 1. Operational Excellence

Busca operar e melhorar continuamente os sistemas e processos.

Pode envolver:

* Monitoramento;
* Processos operacionais;
* Resposta a incidentes;
* Automação;
* Deploy;
* CI/CD;
* Melhoria contínua;
* Aprendizado com incidentes anteriores.

**Palavras-chave:** operar, monitorar, melhorar, automatizar.

---

## 2. Security

Busca proteger sistemas, dados e recursos.

Pode envolver:

* Identidade;
* Autenticação;
* Autorização;
* Controle de acesso;
* Princípio do menor privilégio;
* Proteção de dados;
* Auditoria;
* Rastreabilidade.

**Palavras-chave:** proteger, acesso, permissões, identidade, dados.

---

## 3. Reliability

Busca garantir que um sistema consiga funcionar corretamente, lidar com falhas e se recuperar quando necessário.

Pode envolver:

* Redundância;
* Múltiplas Availability Zones;
* Recuperação de falhas;
* Tolerância a falhas;
* Resiliência;
* Capacidade de recuperação.

**Importante:** **Reliability** é o nome do pilar. Resiliência e tolerância a falhas são conceitos diretamente relacionados a ele.

**Palavras-chave:** falha, recuperação, resiliência, disponibilidade.

---

## 4. Performance Efficiency

Busca utilizar recursos computacionais de maneira eficiente e manter o desempenho adequado.

Pode envolver:

* Escolha adequada dos recursos;
* Monitoramento de performance;
* Escalabilidade;
* Dimensionamento adequado;
* Adaptação à demanda;
* Avaliação de diferentes recursos e tecnologias.

**Palavras-chave:** desempenho, eficiência, recursos adequados, performance.

---

## 5. Cost Optimization

Busca evitar gastos desnecessários e utilizar os recursos financeiros de maneira eficiente.

Pode envolver:

* Identificação de recursos ociosos;
* Dimensionamento adequado;
* Elasticidade;
* Monitoramento dos custos;
* Escolha adequada de modelos de preço;
* Remoção de recursos desnecessários.

Exemplo:

```text
Baixa demanda
     ↓
Reduzir recursos
     ↓
Menor utilização
     ↓
Menor custo
```

**Palavras-chave:** custo, desperdício, recursos ociosos, economia.

---

## 6. Sustainability

Busca reduzir o impacto ambiental relacionado à utilização da infraestrutura.

Pode envolver:

* Redução do consumo de energia;
* Utilização eficiente dos recursos;
* Escolha de arquiteturas mais eficientes;
* Redução de desperdícios computacionais.

**Palavras-chave:** impacto ambiental, energia, eficiência, sustentabilidade.

---

# Relações importantes

## Region → Availability Zones

Uma Region possui múltiplas Availability Zones.

```text
Region
├── AZ A
├── AZ B
└── AZ C
```

Uma AZ pertence a uma única Region.

---

## Availability Zones → High Availability

Distribuir recursos entre diferentes AZs ajuda a evitar que uma falha localizada provoque a indisponibilidade completa da aplicação.

```text
              ┌── EC2 → AZ A
Usuários → LB ┤
              └── EC2 → AZ B
```

---

## Edge Location → CloudFront → Menor latência

O CloudFront utiliza a infraestrutura de Edge Locations para aproximar o conteúdo dos usuários.

```text
Usuário
   ↓
Edge Location
   ↓
CloudFront
   ↓
Origem
```

---

## Scalability → Elasticity

Scalability representa a capacidade de aumentar ou diminuir a capacidade.

Elasticity representa o ajuste dessa capacidade de forma dinâmica conforme a demanda.

```text
Scalability
    ↓
Aumentar / diminuir capacidade

Elasticity
    ↓
Ajustar dinamicamente conforme a demanda
```

---

## High Availability → Múltiplas AZs

Utilizar múltiplas AZs é uma estratégia para aumentar a disponibilidade e reduzir pontos únicos de falha.

```text
              ┌── AZ A → EC2
Usuários → LB ┤
              └── AZ B → EC2
```

---

# Perguntas e respostas

## Perguntas iniciais

### 1. Qual é a diferença entre Region e Availability Zone?

**Minha resposta:**

```
A diferença é conceitual onde uma Region é uma região macro que possui diferentes Availability Zones independentes e uma
AZ pertence a uma única Region exclusivamente
```

**Correção:**

A ideia está correta. Uma Region é uma **área geográfica da AWS** que contém múltiplas Availability Zones. Uma AZ pertence a uma única Region.

---

### 2. Por que distribuir uma aplicação entre duas Availability Zones pode aumentar sua disponibilidade?

**Minha resposta:**

```
Porque se uma das AZs apresentar problema operacional, como a outra AZ está num local isolado diferente, muito provavelmente
ainda estará em funcionamento, o que possibilitará a aplicação em questão continuar funcionando
```

**Conclusão:**

Resposta correta. O isolamento das AZs reduz o impacto de falhas localizadas e pode aumentar a disponibilidade e a resiliência da aplicação.

---

### 3. Qual é a função principal de uma Edge Location e qual serviço da AWS você associa a ela?

**Minha resposta:**

```
Sua principal função é ser um mecanismo de entrega mais rápida de conteúdo para o usuário final, estando bastante relacionada
ao serviço do CloudFront
```

**Correção:**

A ideia está correta. Uma Edge Location é um **ponto de presença da infraestrutura da AWS próximo aos usuários**. O CloudFront utiliza essa infraestrutura para entregar conteúdo com menor latência.

---

### 4. Uma empresa percebe que está gastando muito mais na AWS do que deveria. Qual pilar do Well-Architected Framework deve ser analisado primeiro? Por quê?

**Minha resposta:**

```
O pilar em questão seria o do Otimização de Custos, onde poderia haver análises variadas para tentar identificar recursos
subutilizados ou possibilidade de aplicar técnicas de elasticidade como o Auto Scaling, etc
```

**Conclusão:**

Resposta correta. Recursos ociosos ou superdimensionados podem representar custos desnecessários. A elasticidade pode ajudar a ajustar a capacidade conforme a demanda.

---

### 5. Uma aplicação apresenta falhas frequentemente e precisa ser capaz de se recuperar delas. Qual pilar está diretamente relacionado a esse problema?

**Minha resposta:**

```
O pilar em questão é o da Resiliência, onde poderia haver análises para tentar identificar falhas/interrupções da aplicação
por picos de processamento elevados ou probblemas de AZs, etc
```

**Correção:**

A ideia está correta, mas o nome do pilar é **Reliability (Confiabilidade)**.

Resiliência é um conceito diretamente relacionado à Reliability.

```text
Reliability
    ↓
Resiliência
    ↓
Recuperação / tolerância a falhas
```

---

### 6. Uma equipe quer melhorar o processo de monitoramento, deploy e operação da aplicação. Qual pilar?

**Minha resposta:**

```
O pilar em questão é o de Excelência Operacional, onde se poderia aplicar técnicas de montagens de pipelines CI/CD com
deploy blue/green, monitoramento e notificações a partir de métricas específicas com CloudWatch, etc
```

**Conclusão:**

Resposta correta. Operational Excellence envolve operação, monitoramento, automação, processos, resposta a incidentes e melhoria contínua.

---

### 7. Uma aplicação está funcionando corretamente, mas utiliza recursos computacionais de maneira ineficiente e apresenta baixa performance. Qual pilar?

**Minha resposta:**

```
O pilar em questão seria o de Performance, onde se poderia haver análises e tentar aplicar técnicas de escalonamento horizontal/vertical, redundância, etc
```

**Correção:**

O nome completo do pilar é **Performance Efficiency**.

O escalonamento pode fazer parte da solução. Já a redundância está mais diretamente relacionada à Reliability.

---

# Atividade — Identificação dos pilares

### 8. Permissões maiores que o necessário

Uma aplicação possui dados armazenados em um banco de dados. A equipe percebe que diversos usuários possuem permissões muito maiores do que as necessárias para suas funções.

**Minha resposta:**

```
Pilar de Segurança pois trata de controle de acessos
```

**Conclusão:**

Resposta correta. O problema está relacionado ao controle de acesso e ao princípio do menor privilégio.

---

### 9. Servidores ociosos

Uma aplicação funciona normalmente durante o dia, mas à noite permanece com dezenas de servidores ativos mesmo quando praticamente não há usuários.

**Minha resposta:**

```
Pilar de Otimização de custos porque os recuros pagos estão sendo subutilizados
```

**Conclusão:**

Resposta correta. Existe capacidade sendo mantida sem necessidade, gerando custos desnecessários.

---

### 10. Falha de uma instância

Uma empresa quer garantir que, caso uma instância apresente uma falha, outra instância possa assumir o processamento sem que o sistema inteiro fique indisponível.

**Minha resposta:**

```
Pilar de Confiabilidade pois trata de tolerância a falhas
```

**Conclusão:**

Resposta correta. Reliability está diretamente relacionado à tolerância, recuperação e resiliência diante de falhas.

---

### 11. Instâncias superdimensionadas

Uma equipe percebe que uma aplicação está utilizando instâncias extremamente potentes, mas o processamento médio utiliza apenas uma pequena parte de CPU e memória.

**Minha resposta:**

```
Pilar de Performance porque os recursos estão sendo utilizados errados
```

**Correção:**

A ideia está correta, mas o nome completo é **Performance Efficiency**.

O objetivo é utilizar recursos adequados de maneira eficiente.

---

### 12. Falta de rastreabilidade

Durante uma investigação de segurança, a empresa descobre que não consegue determinar claramente qual usuário realizou determinada alteração em um recurso AWS.

**Minha resposta:**

```
Pilar de Segurança também trata de controle de acessos
```

**Correção:**

O pilar está correto, mas neste caso o ponto principal é **auditoria e rastreabilidade**.

A diferença é:

```text
Quem pode fazer?
    ↓
Controle de acesso

Quem fez?
    ↓
Auditoria / rastreabilidade
```

---

### 13. Processos operacionais

Uma equipe quer estabelecer processos para acompanhar incidentes, melhorar procedimentos operacionais e aprender com problemas que ocorreram em produção.

**Minha resposta:**

```
Pilar de Excelência operacional melhorando a etapa de monitoramento
```

**Conclusão:**

Resposta correta. Operational Excellence envolve monitoramento, processos, resposta a incidentes e melhoria contínua.

---

### 14. Eficiência energética

Uma empresa está escolhendo entre duas arquiteturas tecnicamente capazes de atender à mesma carga. Uma delas utiliza significativamente menos energia e recursos computacionais para entregar o mesmo resultado.

**Minha resposta:**

```
Pilar de Sustentabilidade pois leva em consideração o impato ambiental do recurso escolhido
```

**Conclusão:**

Resposta correta. O foco da situação é o impacto ambiental e a eficiência no uso de recursos.

---

### 15. Aplicação lenta sob carga

Uma aplicação apresenta aumento significativo no tempo de resposta quando o número de requisições cresce. A equipe quer analisar a arquitetura e escolher recursos mais adequados para manter o desempenho.

**Minha resposta:**

```
Pilar de Performance para achar os melhores recursos para o cenário de carga em questão
```

**Conclusão:**

Resposta correta. O nome completo é **Performance Efficiency**, e o objetivo é manter o desempenho utilizando os recursos adequados de maneira eficiente.

---

### 16. Redução de custos com menos instâncias

Uma empresa quer reduzir sua conta da AWS. Uma das propostas é diminuir a quantidade de instâncias durante períodos de baixa utilização.

**Minha resposta:**

```
O pilar principal é o de Otimização de custos onde poderá ser utilizado o conceito de elasticidade
```

**Conclusão:**

Resposta correta.

```text
Cost Optimization
       ↑
       │
Elasticity
       ↑
Ajustar capacidade conforme demanda
```

A elasticidade é o conceito utilizado para ajudar a atingir o objetivo de redução de custos.

---

### 17. Falha de Availability Zone

Uma aplicação precisa continuar disponível mesmo quando ocorre uma falha em uma Availability Zone.

**Minha resposta:**

```
O pilar principal é o de Confiabilidade, onde está diretamente relacionado com o conceito de Tolerância a falhas
```

**Conclusão:**

Resposta correta.

```text
Múltiplas AZs
      ↓
Redução do impacto de falhas
      ↓
Fault Tolerance / Resilience
      ↓
Reliability
```

---

# Resumo para a prova

Ao encontrar uma situação em uma questão, primeiro identifique **qual problema está sendo resolvido**.

| Problema                                           | Pilar                      |
| -------------------------------------------------- | -------------------------- |
| Operação, processos, monitoramento e melhoria      | **Operational Excellence** |
| Proteção, identidade, permissões e auditoria       | **Security**               |
| Falhas, recuperação, resiliência e disponibilidade | **Reliability**            |
| Desempenho e uso eficiente de recursos             | **Performance Efficiency** |
| Custos, desperdícios e recursos ociosos            | **Cost Optimization**      |
| Energia e impacto ambiental                        | **Sustainability**         |

## Palavras-chave

```text
Operar / melhorar
    → Operational Excellence

Proteger / controlar acesso
    → Security

Falha / recuperar / resiliência
    → Reliability

Desempenho / eficiência
    → Performance Efficiency

Custo / desperdício
    → Cost Optimization

Impacto ambiental / energia
    → Sustainability
```

## Principal ponto de atenção

Não confundir:

```text
Reliability
    ≠
Resilience
```

**Reliability** é o pilar.

**Resilience** é uma característica importante de sistemas confiáveis e está diretamente relacionada à capacidade de resistir e se recuperar de falhas.

Também não confundir:

```text
Performance Efficiency
    ≠
Reliability
```

**Performance Efficiency** → o sistema está lento ou os recursos não estão sendo utilizados de maneira eficiente.

**Reliability** → o sistema precisa continuar funcionando ou se recuperar diante de falhas.
