// Datastructures.hh

#ifndef DATASTRUCTURES_HH
#define DATASTRUCTURES_HH

#include <string>
#include <vector>
#include <tuple>
#include <utility>
#include <limits>
#include <functional>
#include <map>
#include <memory>
#include <list>
#include <queue>
#include <stack>

// Types for IDs
using PlaceID = long long int;
using AreaID = long long int;
using Name = std::string;
using WayID = std::string;

// Return values for cases where required thing was not found
PlaceID const NO_PLACE = -1;
AreaID const NO_AREA = -1;
WayID const NO_WAY = "!!No way!!";

// Return value for cases where integer values were not found
int const NO_VALUE = std::numeric_limits<int>::min();

// Return value for cases where name values were not found
Name const NO_NAME = "!!NO_NAME!!";

// Enumeration for different place types
// !!Note since this is a C++11 "scoped enumeration", you'll have to refer to
// individual values as PlaceType::SHELTER etc.
enum class PlaceType { OTHER=0, FIREPIT, SHELTER, PARKING, PEAK, BAY, AREA, NO_TYPE };

// Type for a coordinate (x, y)
struct Coord
{
    int x = NO_VALUE;
    int y = NO_VALUE;
};

// Example: Defining == and hash function for Coord so that it can be used
// as key for std::unordered_map/set, if needed
inline bool operator==(Coord c1, Coord c2) { return c1.x == c2.x && c1.y == c2.y; }
inline bool operator!=(Coord c1, Coord c2) { return !(c1==c2); } // Not strictly necessary

struct CoordHash
{
    std::size_t operator()(Coord xy) const
    {
        auto hasher = std::hash<int>();
        auto xhash = hasher(xy.x);
        auto yhash = hasher(xy.y);
        // Combine hash values (magic!)
        return xhash ^ (yhash + 0x9e3779b9 + (xhash << 6) + (xhash >> 2));
    }
};

// Example: Defining < for Coord so that it can be used
// as key for std::map/set
inline bool operator<(Coord c1, Coord c2)
{
    if (c1.y < c2.y) { return true; }
    else if (c2.y < c1.y) { return false; }
    else { return c1.x < c2.x; }
}

// Return value for cases where coordinates were not found
Coord const NO_COORD = {NO_VALUE, NO_VALUE};

// Type for a distance (in metres)
using Distance = int;

// Return value for cases where Duration is unknown
Distance const NO_DISTANCE = NO_VALUE;

// Enumeration for the last sorter of places
enum SortStatus {Not_sorted, From_origin, Closest_to};

// Enumeration for keeping track of the nodes' colours
enum Colour {White, Gray, Black};


// This is the class you are supposed to implement

class Datastructures
{
public:
    Datastructures();
    ~Datastructures();

    // Estimate of performance:
    // Short rationale for estimate:
    int place_count();

    // Estimate of performance:
    // Short rationale for estimate:
    void clear_all();

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<PlaceID> all_places();

    // Estimate of performance:
    // Short rationale for estimate:
    bool add_place(PlaceID id, Name const& name, PlaceType type, Coord xy);

    // Estimate of performance:
    // Short rationale for estimate:
    std::pair<Name, PlaceType> get_place_name_type(PlaceID id);

    // Estimate of performance:
    // Short rationale for estimate:
    Coord get_place_coord(PlaceID id);

    // We recommend you implement the operations below only after implementing the ones above

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<PlaceID> places_alphabetically();

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<PlaceID> places_coord_order();

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<PlaceID> find_places_name(Name const& name);

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<PlaceID> find_places_type(PlaceType type);

    // Estimate of performance:
    // Short rationale for estimate:
    bool change_place_name(PlaceID id, Name const& newname);

    // Estimate of performance:
    // Short rationale for estimate:
    bool change_place_coord(PlaceID id, Coord newcoord);

    // We recommend you implement the operations below only after implementing the ones above

    // Estimate of performance:
    // Short rationale for estimate:
    bool add_area(AreaID id, Name const& name, std::vector<Coord> coords);

    // Estimate of performance:
    // Short rationale for estimate:
    Name get_area_name(AreaID id);

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<Coord> get_area_coords(AreaID id);

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<AreaID> all_areas();

    // Estimate of performance:
    // Short rationale for estimate:
    bool add_subarea_to_area(AreaID id, AreaID parentid);

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<AreaID> subarea_in_areas(AreaID id);

    // Non-compulsory operations

    // Estimate of performance:
    // Short rationale for estimate:
    void creation_finished();

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<AreaID> all_subareas_in_area(AreaID id);

    // Estimate of performance:
    // Short rationale for estimate:
    std::vector<PlaceID> places_closest_to(Coord xy, PlaceType type);

    // Estimate of performance:
    // Short rationale for estimate:
    bool remove_place(PlaceID id);

    // Estimate of performance:
    // Short rationale for estimate:
    AreaID common_area_of_subareas(AreaID id1, AreaID id2);

    // Phase 2 operations

    // Estimate of performance: O(n)
    // Short rationale for estimate:
    // Two asymptotically constant time operations are executed n times.
    std::vector<WayID> all_ways();

    // Estimate of performance: O(n)
    // Short rationale for estimate:
    // Certain there are operations that may take linear time but are
    // generally faster.
    bool add_way(WayID id, std::vector<Coord> coords);

    // Estimate of performance: O(n)
    // Short rationale for estimate:
    // In the worst case finding the right node and adding departing ways may be
    // linear to nodes' amount.
    std::vector<std::pair<WayID, Coord>> ways_from(Coord xy);

    // Estimate of performance: O(n)
    // Short rationale for estimate:
    // If searching the way takes the worst-case scenario, this has O(n) complexity.
    std::vector<Coord> get_way_coords(WayID id);

    // Estimate of performance: O(n +e)
    // Short rationale for estimate:
    // clear() is linear to the size of the graph and ways as it deletes all their elements
    void clear_ways();

    // Estimate of performance: O(n+e)
    // Short rationale for estimate:
    std::vector<std::tuple<Coord, WayID, Distance>> route_any(Coord fromxy, Coord toxy);

    // Non-compulsory operations

    // Estimate of performance:
    // Short rationale for estimate:
    bool remove_way(WayID id);

    // Estimate of performance: O(n+e)
    // Short rationale for estimate:
    std::vector<std::tuple<Coord, WayID, Distance>> route_least_crossroads(Coord fromxy, Coord toxy);

    // Estimate of performance: O(n+e)
    // Short rationale for estimate:
    std::vector<std::tuple<Coord, WayID>> route_with_cycle(Coord fromxy);

    // Estimate of performance: O(n+e)
    // Short rationale for estimate:
    // In the worst case
    std::vector<std::tuple<Coord, WayID, Distance>> route_shortest_distance(Coord fromxy, Coord toxy);

    // Estimate of performance:
    // Short rationale for estimate:
    Distance trim_ways();

private:
    
    // A struct for saving information of a place
    struct Place {
        PlaceID ID;
        Name const name;
        Coord coordinates;
        PlaceType type;
    };

    // A struct for saving information of an area
    struct Area {
        AreaID ID;
        Name const name;
        std::vector<Coord> coordinates;
        Area* parent = nullptr;
        std::vector<Area*> children = {};
    };

    // Data structure to save place data
    std::unordered_map<PlaceID, std::shared_ptr<Place>> place_data = {};

    // Data structure to save area data
    std::unordered_map<AreaID, std::shared_ptr<Area>> area_data = {};

    // Vector for storing pointers to places
    std::vector<Place*> place_vec = {};
    bool adding_finished = false;

    // Keep track on the order of place_vec
    SortStatus coord_order = Not_sorted;

    // Recursive function used to find areas in which a subarea belongs to
    void parent_area(std::vector<AreaID> &v, AreaID& id);

    // Recursive function to find all subareas in an area
    void subareas(std::vector<AreaID> &v, AreaID& id);

    // Calculate euclidian distance of two coordinates and return it
    double coord_distance(Coord &a, Coord &b);

    // Merging algorithm for coordinates
    void merge(std::vector<Place*>& v,
               unsigned long &left, unsigned long &mid,
               unsigned long &right, Coord &other_coord);

    // Sorting algorithm for the coordinates
    void merge_sort(std::vector<Place*>& v, unsigned long &left,
                    unsigned long &right, Coord &other_coord);

    // Implementation of prg2 starts here
    
    // A struct for a way
    struct Way {
        WayID id;
        std::vector<Coord> vec;
    };

    struct Node {
        Coord coord;
        //std::unordered_map<Node*, std::pair<WayID, Distance>> adjs;
        std::unordered_map<WayID, std::pair<Node*, Distance>> adjs;
        Colour col = White;
        int dist = 0;
        std::pair<Node*, WayID> prev = {nullptr, NO_WAY};
        constexpr bool operator()(const Node* &lhs, const Node* &rhs) const
        {
            return lhs->dist < rhs->dist;
        }

    };

    // Datastructure for saving graph of ways
   std::unordered_map<Coord, std::shared_ptr<Node>, CoordHash> graph;
   // Container for saving all the ways for quick search
   std::unordered_map<WayID, std::shared_ptr<Way>> ways;

   void bfs(std::vector<std::tuple<Coord, WayID, Distance>> & vec,
            Node* node, Coord& goal);

   void dijkstra(std::vector<std::tuple<Coord, WayID, Distance>> & vec,
            Node* node, Coord& goal);
   
   void relax(Node* u, Node*v, Distance w, WayID u_id);

   void dfs(std::vector<std::tuple<Coord, WayID>>& vec, Node* node);
   
};

#endif // DATASTRUCTURES_HH
