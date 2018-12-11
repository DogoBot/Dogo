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
