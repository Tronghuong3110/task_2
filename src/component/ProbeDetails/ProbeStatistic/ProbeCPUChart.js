import Chart from 'chart.js/auto';
import LineChart from './components/LineChart';
import { useEffect, useState } from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css';
import { IP } from '../../Layout/constaints';
const ProbeCPUChart = (props) => {
    const {probeId} = props
    const [performance, setPerformance] = useState({
        labels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15],
        datasets: [{
            label: "CPU",
            data: [],
            backgroundColor: "rgba(216, 255, 2, 0.56)",
            fill: true
        }],
    })
    const [ram, setRAM] = useState({
        labels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15],
        datasets: [{
            label: "RAM",
            data: [],
            backgroundColor: "rgba(226, 34, 251, 0.68)",
            fill: true
        }],
    })
    useEffect(()=>{
        setChartData()
    },[])
    const setChartData = () => {
        setInterval(() => {
            fetch("/api/v1/performance?probeId=" + probeId)
                .then(response => response.json())
                .then(data => {
                    console.log(data)
                    let pArr = []
                    data.map(p => {
                        pArr.push(p.loadAverage.toFixed(2))
                    })
                    let rArr = []
                    data.map(r => {
                        let tmp = (r.memoryRamUsed / r.memoryRamTotal) * 100
                        console.log(tmp.toFixed(2))
                        rArr.push(tmp.toFixed(2))
                    })

                    setPerformance({
                        labels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                        datasets: [{
                            label: "CPU",
                            data: pArr,
                            backgroundColor: "rgba(216, 255, 2, 0.56)",
                            fill: true
                        }],
                    })
                    setRAM({
                        labels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                        datasets: [{
                            label: "RAM",
                            data: rArr,
                            backgroundColor: "rgba(226, 34, 251, 0.68)",
                            fill: true
                        }],
                    })
                })
                .catch(err => console.log(err))
        },5000)
    }
    return (
        <div>
            <Swiper
                spaceBetween={50}
                slidesPerView={1}
            >
                <SwiperSlide>
                    <LineChart chartData={performance} borderColor={"yellow"} />
                </SwiperSlide>
                <SwiperSlide>
                    <LineChart chartData={ram} borderColor={"purple"} />
                </SwiperSlide>
            </Swiper>
        </div>
    )
}
export default ProbeCPUChart;