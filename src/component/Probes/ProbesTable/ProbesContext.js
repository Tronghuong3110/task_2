import { createContext,useRef, useState, useEffect } from "react";
import { IP } from "../../Layout/constaints";

const ProbesContext = createContext()

const ProbesProvider = ({ children }) => {
    // const [openDeleteScreen,setOpenDeleteScreen] = useState(false)
    // const [deletedProbe,setDeletedProbe] = useState({})
    const [probes, setProbes] = useState([])
    const [conditions, setConditions] = useState({
        name: null,
        location: null,
        area: null,
        vlan: null
    });

    const intervalRef = useRef(null);

    useEffect(() => {
        getProbes();
    }, [conditions]);

    const getProbes = () => {
        const fetchData = async () => {
            let { name, location, area, vlan } = conditions;
            let url =
                "http://" +
                IP +
                ":8081/api/v1/probes?" +
                (name ? "name=" + name : "") +
                (location ? "&location=" + location : "") +
                (area ? "&area=" + area : "") +
                (vlan ? "&vlan=" + vlan : "");

            try {
                const response = await fetch(url);
                const data = await response.json();
                console.log(data);
                setProbes(data);
            } catch (err) {
                console.log(err);
            }
        };

        // Clear previous interval
        if (intervalRef.current) {
            clearInterval(intervalRef.current);
        }

        // Fetch data immediately
        fetchData();

        // Fetch data every 5 seconds
        intervalRef.current = setInterval(fetchData, 15000);
    };

    // Clean up interval on component unmount
    useEffect(() => {
        return () => {
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
            }
        };
    }, []);
    const value = {
        probes,
        setProbes,
        getProbes,
        conditions,
        setConditions
    }

    return (
        <ProbesContext.Provider value={value}>
            {children}
        </ProbesContext.Provider>
    )
}

export { ProbesContext, ProbesProvider }