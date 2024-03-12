import { Box, Typography } from '@mui/material'
import React from 'react'

import ChartDisPlay from './ChartDisPlay'
function Core({ number, percent, cpu, probeId, activeCore, setActiveCore }) {
    const setColorForUsage = (percent) => {
        if (percent < 40) return "#4ecb71";
        else if (percent < 80) return "#ffd233";
        else return "#ff1c1c";
    };
    const handleClick = () => {
        if (activeCore === cpu) {
            setActiveCore(null); // Ẩn popup nếu core đã được chọn trước đó
        } else {
            setActiveCore(cpu); // Hiển thị popup của core hiện tại
        }
    };

    return (
        <div style={{ width: "100%",position: "relative" }}  >
            <Box onClick={handleClick}>
                <Box sx={style.core}>
                    <Typography>{number}</Typography>
                    <Typography sx={style.percentText}>{percent}%</Typography>
                </Box>
                <Box sx={style.percentContainer}>
                    <Box
                        sx={{
                            position: 'absolute',
                            height: '100%',
                            background: setColorForUsage(percent),
                            width: `${percent}%`
                        }}
                    ></Box>
                </Box>
            </Box>
            {activeCore === cpu && (
                <ChartDisPlay
                    cpu={cpu}
                    probeId={probeId}
                    setChartDisplay={setActiveCore}
                    
                />
            )}
        </div>
    );
}
/** @type {import("@mui/material").SxProps} */
const style = {
    core: {
        display: 'flex',
        justifyContent: 'space-between',
        cursor: 'pointer'
    },
    percentText: {
        fontSize: '0.8rem',
        display: 'flex',
        alignItems: 'end'
    },
    percentContainer: {
        position: 'relative',
        height: '10px',
        background: '#c1c3cf'
    },
    percent: {
        position: 'absolute',
        height: '100%',
        background: 'red'
    },
    chartContainer: {
        position: "absolute"
    }
}


export default Core