import { Box } from '@mui/material'
import React, { useEffect, useState } from 'react'
import { IP } from '../../../Layout/constaints'
import LineChart from './LineChart'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faX } from '@fortawesome/free-solid-svg-icons';
function ChartDisPlay(props) {
    const { cpu, probeId,setChartDisplay } = props
    const [chartData, setChartData] = useState({
        labels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14],
        datasets: [{
            label: "CPU",
            data: [],
            backgroundColor: "rgba(216, 255, 2, 0.56)",
            fill: true
        }],
    })
    useEffect(() => {
        const fetchCore = async () => {
            fetch(`http://${IP}/api/v1/performance?probeId=${probeId}&number=15`)
                .then(response => response.json())
                .then(data => {
                    let corePercentArr = []
                    for (let dt in data) {
                        for (const key in data[dt]) {
                            if (data[dt].hasOwnProperty(key)) {
                                if (key === cpu)
                                    corePercentArr.push(parseFloat(data[dt][key]).toFixed(2))
                            }
                        }
                    }
                    console.log(corePercentArr)
                    setChartData({
                        labels: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14],
                        datasets: [{
                            label: "CPU",
                            data: corePercentArr,
                            backgroundColor: "rgba(216, 255, 2, 0.56)",
                            fill: true
                        }],
                    })
                })
                .catch(err => console.log(err))
        }
        fetchCore()
        const intervalId = setInterval(fetchCore, 2000)
        return () => {
            clearInterval(intervalId);
        };
    }, [cpu, probeId])
    return (
        <Box sx={style.chartContainer}>
            <Box sx={style.btnContainer}>
                <button onClick={()=>setChartDisplay(null)}><FontAwesomeIcon icon={faX} /></button>
            </Box>
            <LineChart chartData={chartData}></LineChart>
        </Box>
    )
}
/** @type {import("@mui/material").SxProps} */
const style = {
    chartContainer: {
        position: "absolute",
        background: "rgba(39,41,61,0.9)",
        padding: "10px",
        height: "180px",
        width: "320px",
        top: '80%',
        left: "50%",
        zIndex: "2",
        borderRadius: "0 15px 15px 15px"
    },
    btnContainer:{
        display:'flex',
        justifyContent:'flex-end',
        button:{
            background:"transparent",
            border: 'none',
            color: 'red'
        }
    }
    
}
export default ChartDisPlay