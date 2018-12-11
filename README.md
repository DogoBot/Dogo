# Short Description
Dogo is an Open Source <a href="discordapp.com">Discord</a> Bot focused on guild administration and utilities.

# Useful Links
<ul>
  <li><a href="https://discord.gg/gKpCPms">Support Guild</a></li>
  <li><a href="https://github.com/DogoBot/Dogo/wiki">[WIP] Documentation</a></li>
  <li><a>[WIP] Wiki</a></li>
</ul>

# Technologies
<ul>
  <li>Development Language: <a href="https://kotlinlang.org">Kotlin</a> from <a href="https://www.jetbrains.com">JetBrains</a></li>
  <li>Web Server: <a href="https://ktor.io">kTor</a> from <a href="https://www.jetbrains.com">JetBrains</a></li>
  <li>Discord API: <a href="https://github.com/DV8FromTheWorld/JDA">JDA</a> from <a href="https://github.com/DV8FromTheWorld">DV8FromTheWorld</a></li>
  <li>Dependency Mananger: <a href="https://gradle.org">Gradle</a> from <a href="https://gradle.org">Gradle Inc.</a>
  <li>Personal IDE: <a href="https://www.jetbrains.com/idea/">Intellij Idea Ultimate</a> from <a href="https://www.jetbrains.com">JetBrains</a> (thank you JetBrains for Student Pack)</li>
  <li>License: <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache 2.0</a></li>
  <li>VCS: <a href="https://git-scm.com">Git</a> and <a href="https://github.com/">GitHub</a></li>
  <li>SGBD: <a href="https://www.mongodb.com">MongoDB</a> from <a href="https://www.mongodb.com"">MongoDB, Inc.</a></li>
</ul>

# Educational
<p>
Dogo is being made for didatical purposes, it is helping <b>me</b> to learn a lot about the involved tehcnologies, and I hope it will help you too. 
</p>

# For Devs
## Commit Structure
The description of the commits <b>must</b> describe what you did, and should not fix more than one issue per commit;
Everything should be written in English (not perfect, but a undersantable one);
<h3><b>ALL METHODS, FUNCTIONS AND CLASSES MUST BE DOCUMENTED ACCORDING TO <a href="https://kotlinlang.org/docs/reference/kotlin-doc.html"a>kDoc</a>.</b></h3>
<h5>Excluding overriden methods that its function is already explained on superclass. Anyway you should document it if the method do something that is not described on superclass</h5>

## Coding Styling
<ul>
  <li>
    Braces and Bracksts in the same line. Eg.
    
    if(...) {
      ...
    }
    
<b>NOT</b>
    
    if(...)
    {
      ...
    }
    
</li>
    
  <li>
    Spacing after and before ``{`` and ``}``. Eg.
    
    if(...) {
      ...
    }
    
    (...).let { ... }
<b>NOT</b>

    if(...){
      ...
    }
    
    (...).let{...}
    
</li>
<li>
    Use `it` as possible. Eg.
    
    (...).also { it.invoke() }
    <b>NOT</b>
    
    (...).also { m -> m.invoke() }
</li>
<li>Identation</li>
 </ul>

# Copyright
Copyright 2019 Nathan Bombana

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
