import React from 'react'
import { Line } from 'react-chartjs-2'
import styled from 'styled-components'
export default function LineChart({ chartData,borderColor }) {
    return (
        <Line
            data={chartData}
            options={{
                color: "white",
                borderColor:borderColor,
                scales: {
                    x: {
                        type:"linear",
                        grid: {
                            color: "#FFF"
                        },
                        ticks: {
                            // beginAtZero: true,
                            color: 'white',
                            fontSize: 12,
                        }
                    },
                    y:{
                        type:"linear",
                        grid: {
                            color: "#FFF"
                        },
                        ticks: {
                            color: 'white',
                            fontSize: 12,
                            stepSize:25,
                            callback: function(value){
                                return value.toFixed(2)+"%"
                            }
                        },
                    },

                },
            }}
            style={{height:"100%",width:"100%"}}
        ></Line>
    )
}
