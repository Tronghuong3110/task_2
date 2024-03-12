import React, { useEffect, useState } from 'react'
import '../../../sass/ProbeDetails/ProbeStatistic/CoreContainer.scss'
import Core from './components/Core'
import { IP } from '../../Layout/constaints';
function CoreContainer({ probeId }) {
    const [coreList, setCoreList] = useState([]);
    useEffect(() => {
        const fetchCore =() => {
            fetch(IP+`/api/v1/performance?probeId=${probeId}&number=1`)
                .then(response => response.json())
                .then(data => {
                    const coreArr = []
                    for (const key in data[0]) {
                        if (data[0].hasOwnProperty(key)) {
                            if (key.startsWith("cpu") && key !== "cpu" && data[0][key] !== null)
                                coreArr.push({
                                    cpuNumber: key,
                                    percent: data[0][key]
                                })
                        }
                    }
                    setCoreList(coreArr)
                })
                .catch(err => console.log(err))
        }
        fetchCore()
        return () => setInterval(fetchCore, 2000)
    }, [probeId])
    const [activeCore, setActiveCore] = useState(null);
    return (
        <div className="coreContainer">
            {coreList.map((cpu, index) => (
                <Core
                    key={index + 1}
                    number={index}
                    probeId={probeId}
                    cpu={cpu.cpuNumber}
                    percent={cpu.percent !== null ? parseFloat(cpu.percent).toFixed(2) : 0}
                    activeCore={activeCore}
                    setActiveCore={setActiveCore}
                />
            ))}
        </div>
    );
}

export default CoreContainer