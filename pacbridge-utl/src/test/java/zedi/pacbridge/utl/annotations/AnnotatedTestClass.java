package zedi.pacbridge.utl.annotations;

@TestAnnotation
public class AnnotatedTestClass extends MockClass implements MockInterface {

    @TestAnnotation
    private int foo;
    
    @TestAnnotation
    public AnnotatedTestClass() {
    }
    
    @TestAnnotation
    public void foo() {
    }
}
