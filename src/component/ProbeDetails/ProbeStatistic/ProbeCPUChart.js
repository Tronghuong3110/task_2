import Chart from 'chart.js/auto';
import LineChart from './components/LineChart';
import { useEffect, useState, memo } from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css';
import { IP } from '../../Layout/constaints';
import CoreContainer from './CoreContainer';
import { IconButton } from '@mui/material';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
    faArrowLeft
} from '@fortawesome/free-solid-svg-icons'
const ProbeCPUChart = (props) => {
    const { probeId } = props
    const [performance, setPerformance] = useState({
        labels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15],
        datasets: [{
            label: "CPU",
            data: [],
            backgroundColor: "rgba(216, 255, 2, 0.56)",
            fill: true
        }],
    })


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