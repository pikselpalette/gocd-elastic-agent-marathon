package cd.go.contrib.elasticagents.marathon.utils;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SizeTest {

    @Test
    public void sizeShouldNotSurprise() throws Exception {
        assertThat(Size.bytes(2L).toString(), is("2 bytes"));
        assertThat(Size.kilobytes(1L).toString(), is("1 kilobyte"));
        assertThat(Size.megabytes(2L).toString(), is("2 megabytes"));
        assertThat(Size.gigabytes(2L).toString(), is("2 gigabytes"));
        assertThat(Size.terabytes(2L).toString(), is("2 terabytes"));
        assertThat(Size.kilobytes(2L).getQuantity(), is(2L));
        assertThat(Size.kilobytes(2L).getUnit().toString(), is("KILOBYTES"));
    }

    @Test
    public void sizeShouldConvertCorrectly() throws Exception {
        assertThat(Size.bytes(2L).toBytes(), is(2L));
        assertThat(Size.kilobytes(2L).toKilobytes(), is(2L));
        assertThat(Size.megabytes(2L).toMegabytes(), is(2L));
        assertThat(Size.gigabytes(2L).toGigabytes(), is(2L));
        assertThat(Size.terabytes(2L).toTerabytes(), is(2L));
    }

    @Test
    public void sizeShouldCompareCorrectly() throws Exception {
        assertThat(Size.bytes(2L).compareTo(Size.bytes(2L)), is(0));
        assertThat(Size.bytes(2L).compareTo(Size.bytes(1L)), is(1));
        assertThat(Size.bytes(2L).compareTo(Size.bytes(3L)), is(-1));
        assertThat(Size.bytes(2L).compareTo(Size.kilobytes(2L)), is(-1));
    }
}