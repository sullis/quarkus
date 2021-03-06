package io.quarkus.arc.processor;

import static io.quarkus.arc.processor.Basics.index;
import static io.quarkus.arc.processor.Basics.name;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.quarkus.arc.processor.types.Bar;
import io.quarkus.arc.processor.types.Foo;
import io.quarkus.arc.processor.types.FooQualifier;
import java.io.IOException;
import java.util.Collections;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Index;
import org.junit.Test;

/**
 *
 * @author Martin Kouba
 */
public class BeanInfoQualifiersTest {

    @Test
    public void testQualifiers() throws IOException {
        Index index = index(Foo.class, Bar.class, FooQualifier.class);
        DotName fooName = name(Foo.class);
        DotName fooQualifierName = name(FooQualifier.class);
        ClassInfo fooClass = index.getClassByName(fooName);

        BeanInfo bean = Beans.createClassBean(fooClass, new BeanDeployment(index, null, Collections.emptyList()), null);

        AnnotationInstance requiredFooQualifier = index.getAnnotations(fooQualifierName).stream()
                .filter(a -> Kind.FIELD.equals(a.target().kind()) && a.target().asField().name().equals("foo")).findFirst()
                .orElse(null);

        assertNotNull(requiredFooQualifier);
        // FooQualifier#alpha() is @Nonbinding
        assertTrue(Beans.hasQualifier(bean, requiredFooQualifier));
    }

}
