# Velluscinum: A Middleware for BigchainDB

[![](https://jitpack.io/v/chon-group/velluscinum.svg)](https://jitpack.io/#chon-group/velluscinum)


Distributed Ledger Technologies (DLT) characteristics can contribute to several domains, such as Multi-agent Systems (MAS), facilitating the agreement between agents, managing trust relationships, and distributed scenarios. Some contributions to this integration are in the theoretical stage, and the few existing practical contributions have limitations and low performance. This work presents a MAS approach that can use digital assets as a factor of agreement in the relationship between cognitive agents using the Belief-Desire-Intention model. To validate the proposed methodology, we present the middleware Velluscinum that offers new internal actions to agents. The middleware was tested by adapting the Building-a-House classic example to cryptocurrency and agreements mediated by a distributed ledger.

## Using Velluscinum
 With JaCaMo: use the [Velluscinum-JCM Package](https://github.com/chon-group/velluscinum-jcm)


## Importing the Velluscinum Middleware

<details>
<summary> using Maven </summary>

Step 1. Add the JitPack repository to your build file 
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

```

Step 2. Add the dependency
```
<dependency>
    <groupId>com.github.chon-group</groupId>
    <artifactId>velluscinum</artifactId>
    <version>24.2.9</version>
</dependency>
```
</details>

<details>
<summary> using Gradle </summary>
 
Step 1. Add the JitPack repository in your root build.gradle at the end of repositories:
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency
```
dependencies {
        implementation 'com.github.chon-group:velluscinum:24.2.9'
}
```
 
</details>


## COPYRIGHT
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by/4.0/88x31.png" /></a><br />Velluscinum is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/4.0/">Creative Commons Attribution 4.0 International License</a>. The licensor cannot revoke these freedoms as long as you follow the license terms:

* __Attribution__ — You must give __appropriate credit__ like below:

Mori Lazarin, N., Machado Coelho, I., Pantoja, C.E., Viterbo, J. (2023). Velluscinum: A Middleware for Using Digital Assets in Multi-agent Systems. In: Mathieu, P., Dignum, F., Novais, P., De la Prieta, F. (eds) Advances in Practical Applications of Agents, Multi-Agent Systems, and Cognitive Mimetics. The PAAMS Collection. PAAMS 2023. Lecture Notes in Computer Science(), vol 13955. Springer, Cham. DOI: [https://doi.org/10.1007/978-3-031-37616-0_17](https://www.researchgate.net/publication/372282299_Velluscinum_A_Middleware_for_Using_Digital_Assets_in_Multi-agent_Systems)


<details>
<summary> Cite using Bibtex </summary>

```
@InProceedings{velluscinum,
author="Mori Lazarin, Nilson
and Machado Coelho, Igor
and Pantoja, Carlos Eduardo
and Viterbo, Jos{\'e}",
editor="Mathieu, Philippe
and Dignum, Frank
and Novais, Paulo
and De la Prieta, Fernando",
title="{Velluscinum: A Middleware for Using Digital Assets in Multi-agent Systems}",
doi="10.1007/978-3-031-37616-0_17",
booktitle="Advances in Practical Applications of Agents, Multi-Agent Systems, and Cognitive Mimetics. The PAAMS Collection",
year="2023",
publisher="Springer Nature Switzerland",
address="Cham",
pages="200--212",
isbn="978-3-031-37616-0"
}
```
</details>
