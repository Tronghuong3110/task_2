
import {  memo } from 'react';
import 'swiper/css';

import CoreContainer from './CoreContainer';

const ProbeCPUChart = (props) => {
    const { probeId } = props



    return (
        <div style={{ height: "100%" }}>
            {/* {isOpenChart && (<div style={{ height: "100%", display: 'flex', justifyContent: 'center' }}>
                <button onClick={() => setOpenChart(false)}
                    style={{
                        position: "absolute", left: "0", border: "none",
                        fontSize: "1rem",
                        color: "white",
                        background: "transparent"
                    }}
                >
                    <FontAwesomeIcon icon={faArrowLeft} />
                </button>
                <LineChart chartData={performance} borderColor={"yellow"} />
            </div>

            )} */}
            <CoreContainer probeId={probeId} />
        </div>
    )
}
export default memo(ProbeCPUChart);