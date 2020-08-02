import com.fscreene.simpleimmutables.processor.ImmutableTypeVisitor;
import com.squareup.javapoet.TypeName;
import org.junit.jupiter.api.Test;

import javax.lang.model.type.TypeMirror;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class ImmutableTypeVisitorTest {
    @Test
    public void testVisitThrows() {
        assertThatThrownBy(() -> new ImmutableTypeVisitor().visit(mock(TypeMirror.class), mock(TypeName.class)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
