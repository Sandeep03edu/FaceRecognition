import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.tzutalin.dlib.PedestrianDet;
import com.tzutalin.dlib.VisionDetRet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class DLibFunctionsTest {

    private Context mInstrumantationCtx;

    @Before
    public void setup() {
        mInstrumantationCtx = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testFacialLandmark() {
        PedestrianDet peopleDet = new PedestrianDet();
        List<VisionDetRet> results = peopleDet.detect("/sdcard/test.bmp");
        for (final VisionDetRet ret : results) {
            String label = ret.getLabel();
            int rectLeft = ret.getLeft();
            int rectTop= ret.getTop();
            int rectRight = ret.getRight();
            int rectBottom = ret.getBottom();
        }
    }
}
