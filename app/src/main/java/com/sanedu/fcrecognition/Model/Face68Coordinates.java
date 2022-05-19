package com.sanedu.fcrecognition.Model;

import java.util.Arrays;
import java.util.List;

public class Face68Coordinates {

    private static final Integer[] _Jaw = new Integer[]{0, 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    private static final Integer[] _LeftEyebrow = new Integer[]{17,18,19,20,21};
    private static final Integer[] _RightEyebrow = new Integer[]{22,23,24,25,26};
    private static final Integer[] _NoseBridge = new Integer[]{27,28,29,30};
    private static final Integer[] _LowerNose = new Integer[]{30,31,32,33,34,35};
    private static final Integer[] _LeftEye = new Integer[]{36,37,38,39,40,41};
    private static final Integer[] _RightEye = new Integer[]{42,43,44,45,46,47};
    private static final Integer[] _OuterLip = new Integer[]{48,49,50,51,52,53,54,55,56,57,58,59};
    private static final Integer[] _InnerLip = new Integer[]{60,61,62,63,64,65,66,67};
    private static final Integer[] _UpperLip = new Integer[]{48,49,50,51,52,53,54,60,61,62,63,64};
    private static final Integer[] _LowerLip = new Integer[]{55,56,57,58,59,65,66,67};


    public static final List<Integer> JAW = Arrays.asList(_Jaw);
    public static final List<Integer> LEFT_EYEBROW = Arrays.asList(_LeftEyebrow);
    public static final List<Integer> RIGHT_EYEBROW = Arrays.asList(_RightEyebrow);
    public static final List<Integer> NOSE_BRIDGE = Arrays.asList(_NoseBridge);
    public static final List<Integer> LOWER_NOSE = Arrays.asList(_LowerNose);
    public static final List<Integer> LEFT_EYE = Arrays.asList(_LeftEye);
    public static final List<Integer> RIGHT_EYE = Arrays.asList(_RightEye);
    public static final List<Integer> OUTER_LIP = Arrays.asList(_OuterLip);
    public static final List<Integer> INNER_LIP = Arrays.asList(_InnerLip);
    public static final List<Integer> UPPER_LIP = Arrays.asList(_UpperLip);
    public static final List<Integer> LOWER_LIP = Arrays.asList(_LowerLip);


}
